#!/bin/bash
echo Building jar...
jar cvfm out.jar META-INF/MANIFEST.MF *.class *.png
if [ $? -eq 0 ]; then
	echo Success'!'
else
	echo Failed'! :('
fi
