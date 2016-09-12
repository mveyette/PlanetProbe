package org.veyette.planetprobe.env;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Sparky on 9/11/2016.
 */
public class Spring {

    public PointMass p1;
    public PointMass p2;
    public float targetLength;
    public float stiffness;
    public float damping;


    public Spring(PointMass end1, PointMass end2, float _stiffness, float _damping){
        p1 = end1;
        p2 = end2;
        targetLength = p1.position.dst(p2.position)*.99f;
        stiffness = _stiffness;
        damping = _damping;

    }

    public void update(float delta){
        Vector2 x = p1.position.cpy().sub(p2.position);
        float length = x.len();

    if(length > targetLength) {
        float deltaLength = length - targetLength;
        Vector2 norm = x.scl(1/x.len());
        System.out.println(norm.toString());
        Vector2 force = norm.scl(-deltaLength * delta);
        p1.applyForce(force.scl(-delta));
        p2.applyForce(force.scl(delta));
}


    }
}
