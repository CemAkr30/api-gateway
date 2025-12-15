echo off
set version=%1
set branch=release-v%version%

for %%a in ("%cd%") do set "chartPath=%%~na"

echo %version%
git checkout -b %branch%
echo "%branch% olusturuldu!"
yq e -i .springdoc.swagger-ui.custom.version=\"%version%\" src/main/resources/application.yml
yq e -i .appVersion=\"%version%\" helm/%chartPath%/Chart.yaml
yq e -i .version=\"%version%\" gradle.properties
echo "surum duzeltmeleri yapildi"
git add .
git commit -m "%version% release degişiklikleri yapildi"
git push -o merge_request.create -o merge_request.target=master origin release-v%version%
echo "degisiklikler pushlandi"