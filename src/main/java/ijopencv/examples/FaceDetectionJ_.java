package ijopencv.examples;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ijopencv.ij.ImagePlusMatConverter;
import ijopencv.opencv.MatImagePlusConverter;
import ijopencv.opencv.RectRoiConverter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import org.bytedeco.javacpp.opencv_objdetect;
import static org.bytedeco.javacpp.opencv_objdetect.CASCADE_SCALE_IMAGE;

/**
 *
 * @author jonathan
 */
public class FaceDetectionJ_ implements PlugInFilter {

    ImagePlus imp;
    private Object opencv_objdetect;

    @Override
    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return DOES_RGB;
    }

    @Override
    public void run(ImageProcessor ip) {
        File file = new File("haarcascade_frontalface_alt.xml");
        if (!file.exists()) {
            try {
                InputStream link = this.getClass().getResourceAsStream("/resources/haarcascade_frontalface_alt.xml");
                Files.copy(link, file.getAbsoluteFile().toPath());
            } catch (IOException ex) {
                Logger.getLogger(FaceDetectionJ_.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Get the image
        ImagePlus imp = IJ.getImage();
        //Converters
        ImagePlusMatConverter ic = new ImagePlusMatConverter();
        MatImagePlusConverter mip = new MatImagePlusConverter();
        RectRoiConverter rc = new RectRoiConverter();
        opencv_core.Mat img2 = ic.convert(imp,opencv_core.Mat.class);


        // Detect the faces and store them as an array of rectangles
        opencv_core.RectVector rv = detectFaces(img2);

        // Add rectangles to ROI Manager
        RoiManager rm = new RoiManager();
        rm.setVisible(true);
        for (int i = 0; i < rv.size(); i++) {
            Roi r = rc.convert(rv.get(i),Roi.class);
            rm.add(imp, r, 0);
        }

    }

    public opencv_core.RectVector detectFaces(opencv_core.Mat image) {
        opencv_core.Mat img_gray = new opencv_core.Mat();
        cvtColor(image, img_gray, CV_BGR2GRAY);

        opencv_objdetect.CascadeClassifier faceclassifier = new opencv_objdetect.CascadeClassifier(IJ.getDirectory("plugins") + "haarcascade_frontalface_alt.xml");

        opencv_core.RectVector rv = new opencv_core.RectVector();

        faceclassifier.detectMultiScale(img_gray, rv, 1.1, 2, CASCADE_SCALE_IMAGE, new opencv_core.Size(30, 30), new opencv_core.Size(500, 500));

        return rv;

    }

}
