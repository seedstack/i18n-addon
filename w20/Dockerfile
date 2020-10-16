#
# Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
#
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
#

FROM node AS build
WORKDIR /app
COPY . .
RUN yarn build

FROM nginx:stable
COPY --from=build /app/build/ /var/www
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
CMD ["nginx -g 'daemon off;'"]
