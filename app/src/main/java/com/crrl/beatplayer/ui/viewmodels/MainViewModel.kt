/*
 * Copyright (c) 2020. Carlos René Ramos López. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crrl.beatplayer.ui.viewmodels

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.view.animation.AccelerateInterpolator
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crrl.beatplayer.databinding.ActivityMainBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.playback.PlaybackConnection
import com.crrl.beatplayer.repository.FavoritesRepository
import com.crrl.beatplayer.ui.viewmodels.base.CoroutineViewModel
import com.crrl.beatplayer.utils.BeatConstants.QUEUE_LIST_TYPE_KEY
import com.crrl.beatplayer.utils.BeatConstants.UPDATE_QUEUE
import com.crrl.beatplayer.utils.SettingsUtility
import com.crrl.beatplayer.utils.SettingsUtility.Companion.QUEUE_LIST_KEY
import com.github.florent37.kotlin.pleaseanimate.please
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class MainViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val PlaybackConnection: PlaybackConnection
) : CoroutineViewModel(Main) {

    private val isFavLiveData = MutableLiveData<Boolean>()
    private val isAlbumFavLiveData = MutableLiveData<Boolean>()
    private val isSongFavLiveData = MutableLiveData<Boolean>()

    lateinit var binding: ActivityMainBinding

    fun mediaItemClicked(mediaItem: MediaBrowserCompat.MediaItem, extras: Bundle?) {
        transportControls()?.playFromMediaId(mediaItem.mediaId, extras)
    }

    fun transportControls() = PlaybackConnection.transportControls

    fun reloadQueueIds(ids: LongArray, type: String) {
        transportControls()?.sendCustomAction(
            UPDATE_QUEUE,
            bundleOf(QUEUE_LIST_KEY to ids, QUEUE_LIST_TYPE_KEY to type)
        )
    }

    fun isFav(id: Long): LiveData<Boolean> {
        launch {
            val isFav = withContext(IO) {
                favoritesRepository.favExist(id)
            }
            isFavLiveData.postValue(isFav)
        }
        return isFavLiveData
    }

    fun isAlbumFav(id: Long): LiveData<Boolean> {
        launch {
            val isFav = withContext(IO) {
                favoritesRepository.favExist(id)
            }
            isAlbumFavLiveData.postValue(isFav)
        }
        return isAlbumFavLiveData
    }

    fun isSongFav(id: Long): LiveData<Boolean> {
        launch {
            val isFav = withContext(IO) {
                favoritesRepository.songExist(id)
            }
            isSongFavLiveData.postValue(isFav)
        }
        return isSongFavLiveData
    }

    fun hideMiniPlayer() {
        binding.apply {
            bottomControls.isEnabled = false
            please(190, AccelerateInterpolator()) {
                animate(bottomControls) {
                    belowOf(mainContainer)
                }
            }.start()
        }
    }

    fun showMiniPlayer() {
        binding.apply {
            bottomControls.isEnabled = true
            please(190, AccelerateInterpolator()) {
                animate(bottomControls) {
                    bottomOfItsParent()
                }
            }.start()
        }
    }
}

