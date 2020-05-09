package com.grean.vehicledataviewer.model;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifeng on 2020/5/9.
 */

public class TrackFormat {
    private float rang = 5f;
    private List<LatLng> points = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private ColorsConverter colorsConverter = new ColorsConverter();


    /** HSV转RGB
     * h = 0~359 s=0~1 v= 0~1
     */
    private class ColorsConverter{
        private float r,g,b,s=0.5f,v=0.5f;
        private int h;

        public void converter(){
            int hi = Math.floorMod( (h/60),6);
            float f = h/60-hi;
            float p = v*(1-s);
            float q = v*(1-f*s);
            float t = v*(1-(1-f)*s);
            switch (hi){
                case 0:
                    r = v;
                    g = t;
                    b = p;
                    break;
                case 1:
                    r = q;
                    g = v;
                    b = p;
                    break;
                case 2:
                    r = p;
                    g = v;
                    b = t;
                    break;
                case 3:
                    r = p;
                    g = q;
                    b = v;
                    break;
                case 4:
                    r = t;
                    g = p;
                    b = v;
                    break;
                case 5:
                    r = v;
                    g = p;
                    b = q;
                    break;
                default:
                    break;
            }
        }

        public int getColor(){
            int result = 0x80000000;
            int iR= (int) (r*255)<<16;
            int iG= (int) (g*255)<<8;
            int iB= (int) (b*255);
            return result+iR+iG+iB;
        }

        public void setH(int h) {
            this.h = h;
        }

        public void setS(float s) {
            this.s = s;
        }

        public void setV(float v) {
            this.v = v;
        }

    }

}
