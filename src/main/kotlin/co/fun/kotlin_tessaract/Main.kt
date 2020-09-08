package co.`fun`.kotlin_tessaract

import net.sourceforge.tess4j.ITessAPI
import net.sourceforge.tess4j.Tesseract
import nu.pattern.OpenCV
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

fun main() {
    setupOpenCV()
    setupTesseract()

    val image = ImageIO.read(URL("http://img.ifcdn.com/images/b313c1f095336b6d681f75888f8932fc8a531eacd4bc436e4d4aeff7b599b600_1.jpg"))
    val mat = image.toMat()

    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)
    Imgproc.threshold(mat, mat, 244.0, 255.0, Imgproc.THRESH_BINARY)
    Core.bitwise_not(mat, mat)

    val preparedImage = mat.toBufferedImage()

    val api = Tesseract()
    api.setDatapath("/usr/local/share/tessdata/")
    api.setLanguage("eng")

    val result = api.getWords(preparedImage, ITessAPI.TessPageIteratorLevel.RIL_WORD)
    println(result)
}

private fun setupTesseract() {
    val libPath = "/usr/local/lib"
    val libTess = File(libPath, "libtesseract.dylib")
    if (libTess.exists()) {
        val jnaLibPath = System.getProperty("jna.library.path")
        if (jnaLibPath == null) {
            System.setProperty("jna.library.path", libPath)
        } else {
            System.setProperty("jna.library.path", libPath + File.pathSeparator + jnaLibPath)
        }
    }
}

private fun setupOpenCV() {
    OpenCV.loadLocally()
}

private fun BufferedImage.toMat(): Mat {
    val pixels = (raster.dataBuffer as DataBufferByte).data
    return Mat(height, width, CvType.CV_8UC3)
        .apply { put(0, 0, pixels) }
}

private fun Mat.toBufferedImage(): BufferedImage {
    var type = BufferedImage.TYPE_BYTE_GRAY
    if (channels() > 1) {
        type = BufferedImage.TYPE_3BYTE_BGR
    }
    val bufferSize = channels() * cols() * rows()
    val b = ByteArray(bufferSize)
    this[0, 0, b] // get all the pixels
    val image = BufferedImage(cols(), rows(), type)
    val targetPixels = (image.raster.dataBuffer as DataBufferByte).data
    System.arraycopy(b, 0, targetPixels, 0, b.size)
    return image
}
