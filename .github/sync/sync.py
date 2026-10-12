#!/usr/bin/env python3
"""Apply reviewed changes to the latest successful upstream main build."""
import json
import os
import pathlib
import subprocess
import tempfile

ROOT = pathlib.Path(__file__).resolve().parents[2]

def run(*args, cwd=ROOT):
    return subprocess.check_output(args, cwd=cwd, text=True).strip()

repo = os.environ['GITHUB_REPOSITORY']
response = json.loads(run('gh', 'api', 'repos/tiann/KernelSU/actions/workflows/build-manager.yml/runs?branch=main&event=push&status=success&per_page=1'))
if not response['workflow_runs']:
    raise SystemExit('No successful upstream main push build found')
source = response['workflow_runs'][0]
sha = source['head_sha']
branch = 'sync/upstream-' + sha
if os.environ.get('SYNC_DRY_RUN') != '1' and run('git', 'ls-remote', '--heads', 'origin', 'refs/heads/' + branch):
    builds = json.loads(run('gh', 'api', f'repos/{repo}/actions/workflows/custom-manager.yml/runs?branch={branch}&event=workflow_dispatch&per_page=10'))['workflow_runs']
    if any(build['status'] != 'completed' or build['conclusion'] == 'success' for build in builds):
        print('Build active or already successful:', branch)
    elif len(builds) >= 3:
        raise SystemExit('Three build attempts failed; manual review required: ' + branch)
    else:
        run('gh', 'workflow', 'run', 'custom-manager.yml', '--repo', repo, '--ref', branch)
        print('Retry dispatched:', branch)
    raise SystemExit(0)
run('git', 'fetch', '--tags', 'https://github.com/tiann/KernelSU.git', sha)
with tempfile.TemporaryDirectory(prefix='ksu-sync-', dir=ROOT.parent) as tmp:
    target = pathlib.Path(tmp) / 'source'
    run('git', 'worktree', 'add', '--detach', str(target), sha)
    try:
        run('git', 'apply', '--3way', str(ROOT / '.github/sync/custom.patch'), cwd=target)
        run('git', 'diff', '--check', cwd=target)
        run('git', 'diff', '--cached', '--check', cwd=target)
        workflow = target / '.github/workflows/custom-manager.yml'
        workflow.write_bytes((ROOT / '.github/workflows/custom-manager.yml').read_bytes())
        metadata = dict(upstream_repo='tiann/KernelSU', upstream_sha=sha, upstream_run=source['id'], upstream_url=source['html_url'], customization_sha=run('git', 'rev-parse', 'HEAD'))
        (target / 'sync-provenance.json').write_text(json.dumps(metadata, indent=2) + '\n')
        run('git', 'config', 'user.name', 'KernelSU sync bot', cwd=target)
        run('git', 'config', 'user.email', '41898282+github-actions[bot]@users.noreply.github.com', cwd=target)
        run('git', 'add', '--all', cwd=target)
        run('git', 'commit', '-m', 'Apply customization to upstream ' + sha[:12], cwd=target)
        if os.environ.get('SYNC_DRY_RUN') == '1':
            print('DRY RUN PASS:', json.dumps(metadata))
        else:
            run('git', 'push', 'origin', 'HEAD:refs/heads/' + branch, cwd=target)
            run('gh', 'workflow', 'run', 'custom-manager.yml', '--repo', repo, '--ref', branch)
            print('Build dispatched:', branch, source['html_url'])
    finally:
        run('git', 'worktree', 'remove', '--force', str(target))
