#!/usr/bin/env bash
# NDK r30 libc.a includes Rust's personality symbol. Prefer the application's
# definition when statically linking a Rust binary, without changing the NDK.
set -eu
triple=$1
llvm="$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64"
libc_dir="$PWD/target/ndk-static-libc/$triple"
mkdir -p "$libc_dir"
cp "$llvm/sysroot/usr/lib/$triple/libc.a" "$libc_dir/libc.a"
"$llvm/bin/llvm-objcopy" --weaken-symbol=rust_eh_personality "$libc_dir/libc.a"
export RUSTFLAGS="${RUSTFLAGS:-} -L native=$libc_dir"
