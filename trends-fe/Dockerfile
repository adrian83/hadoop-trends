FROM node:8.16.0-jessie-slim

ADD . /trends-fe
WORKDIR /trends-fe
RUN rm -rf node_modules

RUN echo "REACT_APP_BE_HOST=http://backend:8080" > .env
RUN cat .env

RUN npm --version
RUN ls -al
RUN pwd
CMD ["npm", "start"]

