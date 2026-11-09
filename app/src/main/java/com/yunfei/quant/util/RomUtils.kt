package com.yunfei.quant.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

enum class RomBrand(val display: String) {
    XIAOMI("小米 / Redmi / 澎湃OS"),
    VIVO("vivo / iQOO"),
    OPPO("OPPO / realme"),
    HUAWEI("华为 / 荣耀"),
    OTHER("其他"),
}

object RomUtils {

    val brand: RomBrand by lazy {
        val b = (Build.BRAND + " " + Build.MANUFACTURER).lowercase()
        when {
            b.contains("xiaomi") || b.contains("redmi") || b.contains("poco") -> RomBrand.XIAOMI
            b.contains("vivo") || b.contains("iqoo") -> RomBrand.VIVO
            b.contains("oppo") || b.contains("realme") || b.contains("oneplus") -> RomBrand.OPPO
            b.contains("huawei") || b.contains("honor") -> RomBrand.HUAWEI
            else -> RomBrand.OTHER
        }
    }

    fun steps(brand: RomBrand): List<String> = when (brand) {
        RomBrand.XIAOMI -> listOf(
            "设置 → 应用设置 → 应用管理 → 云飞量化",
            "「自启动管理」→ 打开允许自启动",
            "「省电策略」→ 选择「无限制」",
            "最近任务里下拉本应用 → 锁定后台",
        )
        RomBrand.VIVO -> listOf(
            "设置 → 电池 → 后台耗电管理 → 云飞量化 → 允许后台高耗电",
            "设置 → 应用与权限 → 权限管理 → 自启动 → 打开",
            "i管家 → 应用管理 → 自启动管理 → 允许",
            "最近任务里下拉本应用 → 锁定",
        )
        RomBrand.OPPO -> listOf(
            "设置 → 电池 → 应用耗电管理 → 云飞量化 → 允许后台运行",
            "设置 → 应用管理 → 自启动管理 → 打开",
            "最近任务里下拉本应用 → 锁定",
        )
        RomBrand.HUAWEI -> listOf(
            "设置 → 电池 → 启动管理 → 手动管理 → 全部允许",
            "设置 → 应用 → 应用启动管理 → 关闭自动管理",
            "最近任务里下拉本应用 → 锁定",
        )
        RomBrand.OTHER -> listOf(
            "设置 → 应用管理 → 云飞量化 → 电池 → 不受限制",
            "允许后台活动、允许自启动",
        )
    }

    fun openAppDetails(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }

    fun openAutoStart(context: Context) {
        val candidates = when (brand) {
            RomBrand.XIAOMI -> listOf(
                ComponentName("com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"),
            )
            RomBrand.VIVO -> listOf(
                ComponentName("com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"),
                ComponentName("com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"),
            )
            RomBrand.OPPO -> listOf(
                ComponentName("com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"),
            )
            RomBrand.HUAWEI -> listOf(
                ComponentName("com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"),
            )
            RomBrand.OTHER -> emptyList()
        }
        for (cn in candidates) {
            val intent = Intent().apply {
                component = cn
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (context.packageManager.resolveActivity(intent, 0) != null) {
                runCatching { context.startActivity(intent); return }
            }
        }
        openAppDetails(context)
    }

    fun requestIgnoreBatteryOptimization(context: Context) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
            .onFailure { openAppDetails(context) }
    }
}
