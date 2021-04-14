package View.spriteManagers;

import Model.Position;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StaticPicturePartManager extends SpriteManager{
    private final BufferedImage img;

    public StaticPicturePartManager(String imgPath, Position blockSize, Rectangle imgPart) {
        super(blockSize);
        BufferedImage i= null;
        try {
            i = ImageIO.read(new File(imgPath));
            i=cropImage(i,imgPart);
            i=resize(i, blockSize.getX_asPixel(), blockSize.getY_asPixel());
        } catch (IOException e) {
            System.err.println(imgPath+" not found");
        }
        img=i;
    }

    @Override
    public BufferedImage nextSprite() {
        return img;
    }
}