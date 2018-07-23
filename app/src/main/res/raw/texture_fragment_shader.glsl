precision mediump float;

uniform sampler2D u_TextureUnit;
uniform sampler2D u_Background;
varying vec2 v_TextureCoordinates;


void main()
{
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates) + texture2D(u_Background, v_TextureCoordinates);
}