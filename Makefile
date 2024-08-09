BUILDCONTAINER = "gradle:8.7.0-jdk17-alpine"

make:
	rm -rf ./build
	docker run --rm -u ${shell id -u}:${shell id -g} -v ${shell pwd}:/home/gradle ${BUILDCONTAINER} gradle build
	mv ./build/libs/burp-update-headers-1.0.jar .