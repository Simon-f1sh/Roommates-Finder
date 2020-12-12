if [ ! -d ../backend/src/main/resources ]; then 
    mkdir ../backend/src/main/resources
fi
if [ ! -d ../backend/src/main/resources/web ]; then
    mkdir ../backend/src/main/resources/web
fi
cp ./index.html ../backend/src/main/resources/web/index.html
cp ./search.html ../backend/src/main/resources/web/search.html
cp ./profile.html ../backend/src/main/resources/web/profile.html
cp ./styles.css ../backend/src/main/resources/web/.
cp ./main.js ../backend/src/main/resources/web/.
cp -r ./node_modules ../backend/src/main/resources/web/.