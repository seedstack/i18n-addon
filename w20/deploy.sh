#
# Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
#
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
#

if [ $TRAVIS_PULL_REQUEST = "false" ]
then 
	echo "Not a PR => push"
	if [ "$TRAVIS_TAG" != "" ]
	then
		echo "It's a tag"
		docker tag i18n-ui:$COMMIT $DOCKER_REPO/i18n-ui:$TRAVIS_TAG
		docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
		docker push $DOCKER_REPO/i18n-ui:$TRAVIS_TAG

	else
		echo "Not a tag"
		export TAG=`if [ "$TRAVIS_BRANCH" == "master" ]; then echo "latest"; else echo $TRAVIS_BRANCH ; fi`
		docker tag i18n-ui:$COMMIT $DOCKER_REPO/i18n-ui:$TAG
		docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
		docker push $DOCKER_REPO/i18n-ui:$TAG
	fi
else 
	echo "PR => skip push"
fi
