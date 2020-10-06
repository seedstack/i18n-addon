#
# Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
#
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
#

FROM openjdk:14-alpine
EXPOSE 8080
ADD target/i18n-service-capsule.jar /app/i18n-service-capsule.jar
ENTRYPOINT java $JAVA_OPTS -jar /app/i18n-service-capsule.jar
