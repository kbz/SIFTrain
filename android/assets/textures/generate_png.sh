#!/bin/bash
# auto-convert editable SVGs to baked pixel PNGs
# need Inkscape installed

inkscape -z textures.pack.svg -e=textures.pack.png
inkscape -z textures.pack2.svg -e=textures.pack2.png
inkscape -z textures.pack3.svg -e=textures.pack3.png
