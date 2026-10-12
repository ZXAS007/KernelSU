# KernelSU 定制与同步约定

本文件记录已确认的定制要求，供后续同步、维护与验收使用。

- Manager 与 ksud 包名统一为 `op.ultra.plus`。
- Material 与 Miuix 设置页均提供 SELinux 强制模式开关。通过 root shell 读取与修改状态，操作期间禁用开关，修改后重新读取并通知首页刷新；失败显示提示。修改仅本次运行有效，重启恢复。
- Manager 版本名使用上游来源提交的官方 `git describe --tags --match "v[0-9]*" --long --abbrev=8` 格式。同步提交和定制提交不应改变该版本名；版本随实际选取的上游提交变化。
- GitHub Actions 使用固定云端签名，仅启用 APK v2。固定证书 SHA256 为 `7c74bb17b346162e0a0c8f424ebb32f1ba61e14c44658c33beb2d6dd2a195439`，DER 大小 779 字节。仓库仅保存公钥证书；私钥与密码存放 GitHub secrets。下载的 Actions APK 保留云端签名。
- LKM 预期证书与 Manager 签名一致。每次交付构建同源 LKM、ksuinit、三种 ABI ksud，并检查 APK 中 ksud 与本次构建载荷逐字节相同。
- 同步选择上游成功构建对应的源码，将定制补丁严格以三方方式应用。同步分支包含上游 SHA 与定制 SHA，定制更新必须允许同一上游重新构建。固定证书、同步配置、工作流与来源信息必须传递到同步分支。
- 定制源码、签名公钥与同步补丁必须提交到 fork。修改 `.github/scripts`、`.github/workflows` 或 `manager` 后，应重新生成 `.github/sync/custom.patch` 并验证补丁可应用。
- 本地与云端 Manager 使用 `--warning-mode=fail`；不以警告抑制代替修复。第三方 Actions 的运行时警告需单独记录。
- 非需求区域尽量保持上游源码一致；构建目录使用 `/data/adb/AI/`，成品和备份使用 `/storage/emulated/0/AI/`。

## 已验证交付

源码修复提交：`ccc5be57344b4b170b2fd1955bb08370e1afcbee`（前序签名与配置修复为 `abf92f7f46fc1152ad8e251f6281077c5d109f29`）。

- 同步运行：38203659813，成功。
- 构建运行：38203673650，全部成功。
- 上游 SHA：`0c0fdb960178fa12d80d6cbcfdcb6ad8be6a865d`。
- APK：`KernelSU_v3.3.0-79-g0c0fdb96_32681-release.apk`。
- APK SHA256：`9d26b98c8e808070412cda8d7b26033db5495e3d63683d82958c18d1536ba1b8`。
- 已通过本地 release 编译、云端 clean release 编译、固定证书检查、仅 v2 签名检查、包名/版本检查、三 ABI 载荷哈希复核与云端来源检查。

## 待验证与已知外部提示

- 修复后尚无真实 schedule 成功记录；工作流为 active，手动同步闭环已成功。真实 schedule 需要由 GitHub 调度产生，不能用手动派发代替。
- SELinux 开关及首页联动尚未在设备界面实测，代码编译成功不代表功能实测完成。
- 云端 Manager 编译未出现 Gradle 配置/编译警告；下载载荷的第三方 `actions/download-artifact@v8` 输出 Node `DEP0005`（旧 Buffer 构造器）弃用警告，另有 Git 默认分支名称提示。这些不属于 Manager 编译器警告。
- 尚未执行构建缓存清理；清理须确认成品和备份已保存，只处理任务产生的数据。
