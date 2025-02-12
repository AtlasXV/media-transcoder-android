package com.otaliastudios.transcoder.ext

import com.otaliastudios.transcoder.TranscoderOptions

/**
 * weiping@atlasv.com
 * 3/25/21
 */
interface TranscodeOptionFactory {
    fun create(currentRetryTimes: Int): TranscoderOptions
    fun maxRetryTimes(): Int
}