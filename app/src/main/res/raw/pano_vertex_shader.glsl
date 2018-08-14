attribute vec3 a_Position;
attribute vec2 a_TexCoordinate;
varying vec2 v_TexCoordinate;
uniform mat4 u_MVPMatrix;
void main()
{
    gl_Position = u_MVPMatrix * vec4(a_Position, 1.0);
    v_TexCoordinate = a_TexCoordinate;
}