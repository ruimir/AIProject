package Agents.Utils;

import java.awt.geom.Point2D;

public class Vec2 extends  Point2D.Float {
    public Vec2(float x, float y){
        super.x = x;
        super.y = y;
    }


    public float getLenght(){
        return (float)Math.sqrt(x * x + y * y);
    }

    public Vec2 normalize(){
        float l = getLenght();
        if(getLenght() != 0){ //just 0 division failproof
            x = x/l;
            y = y/l;
        }
        return this;
    }

    public Vec2 addMe(Vec2 v){
        x += v.x;
        y += v.y;
        return this;
    }

    public static Vec2 subtract(Vec2 s1, Vec2 s2){
       return new Vec2(s1.x - s2.x, s1.y - s2.y);
    }

    public static Vec2 add(Vec2 s1, Vec2 s2){
        return new Vec2(s1.x + s2.x, s1.y + s2.y);
    }

    public static Vec2 multiply(Vec2 vec, float f){
        return new Vec2(vec.x * f, vec.y * f);
    }
}
