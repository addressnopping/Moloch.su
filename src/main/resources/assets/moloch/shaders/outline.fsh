#version 120


uniform sampler2D texture;
uniform vec2 texelSize;

uniform vec4 color;
uniform float alpha;
uniform float radius;

void main() {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

    if (centerCol.a != 0) {
        gl_FragColor = vec4(0, 0, 0, 0);
    } else {
        for (float x = -radius; x <= radius; x++) {
            for (float y = -radius; y <= radius; y++) {
                vec4 currentColor = texture2D(texture, gl_TexCoord[0].xy + vec2(texelSize.x * x, texelSize.y * y));

                if (currentColor.a != 0) {
                    gl_FragColor = color;
                    gl_FragColor.a *= alpha;
                }
            }
        }
    }
}