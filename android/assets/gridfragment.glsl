#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform mat4 u_projTrans;
uniform sampler2D u_texture;

void main()
{
   lowp vec4 col = texture2D( texture, v_texCoords ) * v_color;

    float x,y;
    x=fract(v_texCoords.x*25.0);
    y=fract(v_texCoords.y*25.0);

    // Draw a black and white grid.
    if(x > 0.9 || y > 0.9) {
        gl_FragColor = vec4(1,1,1,1);
    }
    else
    {
        gl_FragColor = vec4(0,0,0,0);
    }
}