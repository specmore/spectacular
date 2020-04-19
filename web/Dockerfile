FROM node:12.14.0 AS build

# Create app directory
WORKDIR /usr/src/app

# Install app dependencies
COPY package*.json ./
RUN npm install

# Copy webpack config
COPY *.config.js ./

# Copy app source & test mocks
COPY src src
COPY __mocks__ __mocks__

# Test
RUN npm run test

# Set version
ARG SEMVER
ARG SHORTSHA
ENV SEMVER ${SEMVER:-local}
ENV SHORTSHA ${SHORTSHA:-local}
RUN echo $SEMVER
RUN echo $SHORTSHA

# Bundle
RUN npm run build

###
FROM nginx:1.16.1

ENV API_LOCATION=http://backend

# Copy bundled web files
COPY --from=build /usr/src/app/dist /usr/share/nginx/html

# Copy and web config template
COPY web.template.conf /etc/nginx/conf.d/web.template

CMD ["bash", "-c", "envsubst '$${API_LOCATION},$${AUTH_LOCATION},$${GITHUB_APP_INSTALLATION_ID}' < /etc/nginx/conf.d/web.template > /etc/nginx/conf.d/default.conf && exec nginx -g 'daemon off;'"]
