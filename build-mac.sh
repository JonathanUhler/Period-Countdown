javapackager \
  -deploy -Bruntime= \
  -native image \
  -srcdir . \
  -srcfiles PeriodCountdown.jar \
  -outdir release \
  -outfile PeriodCountdown.app \
  -appclass main.PeriodCountdown \
  -name "PeriodCountdown" \
  -title "PeriodCountdown" \
  -Bicon=reference/icon.icns \
  -nosign \
  -v


echo "Moving lib/ and json/ into app contents..."
cp -R lib release/bundles/PeriodCountdown.app/Contents/Java/
cp -R json release/bundles/PeriodCountdown.app/Contents/Java/


echo "Creating app executable..."
rm release/bundles/PeriodCountdown.app/Contents/MacOS/PeriodCountdown
printf "#! /bin/bash
SH_PATH=\"\$0\"
SH_PARENT=\`dirname \${SH_PATH}\`
PR_PARENT=\`dirname \${SH_PARENT}\`
JAR_PATH=\"\${PR_PARENT}/Java/PeriodCountdown.jar\"
java -jar \${JAR_PATH}" > release/bundles/PeriodCountdown.app/Contents/MacOS/PeriodCountdown
chmod +x release/bundles/PeriodCountdown.app/Contents/MacOS/PeriodCountdown


echo "Moving app to release/"
rm -r release/PeriodCountdown.app
mv release/bundles/PeriodCountdown.app release


echo "Cleaning up release/"
rm -r release/bundles
rm release/PeriodCountdown.app.html
rm release/PeriodCountdown.app.jnlp
rm release/PeriodCountdown.jar