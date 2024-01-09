package com.jr.sudokungfu

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
class FreeStorageManager(context: Context) {
    private val storageStatsManager =
        context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
    private val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

    private val _freeStorageMb = MutableStateFlow(getFreeStorageMb())
    val freeStorageMb: Flow<Long> = _freeStorageMb

    private fun getFreeStorageMb(): Long {
        var freeBytes = 0L
        val storageVolumes: List<StorageVolume> = storageManager.storageVolumes
        for (storageVolume in storageVolumes) {
            val uuid = if (storageVolume.uuid.isNullOrBlank()) {
                UUID.fromString(storageVolume.uuid)
            } else {
                StorageManager.UUID_DEFAULT
            }
            if (storageVolume.isPrimary && storageVolume.isRemovable) {
                freeBytes += storageStatsManager.getFreeBytes(uuid)
            }
        }
        return freeBytes.div(1024).div(1024)
    }
}