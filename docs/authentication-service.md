# User Authentication Service
The User Authentication Service (or Auth Service) provides the functionality that allows users to be able to login to the Spectacular tool using their own user account on their git hosting solution.

Spectacular uses an opensource application called [loginsrv](https://github.com/tarent/loginsrv) as its User Authentication Service.

## Config
The `loginsrv` application offers many different configurable options as shown on their GitHub project's [README](https://github.com/tarent/loginsrv/blob/master/README.md#config-options). 

However, to use it with the Spectacular as the Authentication Service,the following configuration should be set as Environment Variables before starting the application:
- `LOGINSRV_GITHUB` - A single string of text following the pattern `client_id=<Client ID>,client_secret=<Client secret>`. Where the `<Client ID>` and `<Client secret>` tokens are substituted with the `Client ID` and `Client secret` values found on your GitHub App's "General" page.
- `LOGINSRV_JWT_SECRET` - A 32-byte text value. e.g. `this_test_shared_key_is_32_bytes`

## Running locally
The `loginsrv` application is distributed as a docker image and can easily be run locally as a docker container. To start the container with the necessary Environment Variable configurations as set out above, using the following command:

```
docker run -p 5001:8080 -e LOGINSRV_GITHUB="client_id=<Client ID>,client_secret=<Client secret>" -e LOGINSRV_JWT-SECRET=this_test_shared_key_is_32_bytes -e LOGINSRV_COOKIE_HTTP_ONLY=false -e LOGINSRV_COOKIE_SECURE=false tarent/loginsrv
```

For development purposes it is recommended to set the `LOGINSRV_COOKIE_HTTP_ONLY` and `LOGINSRV_COOKIE_SECURE` configuration options to false.
