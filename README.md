# NanoURL - URL shortener service

## How to run

### Build
```bash
$ docker-compose build 
```

### Service
```bash
$ docker-compose up nanourl 
```

### Tests
```bash
$ docker-compose run tests 
```

## How to use

### Create short URL
```bash
$ curl --location --request POST 'http://localhost:8080/' \
  --header 'Content-Type: application/json' \
  --data-raw '{
  	"url": "https://www.google.com"
  }'
```

This will respond with

```json
{
  "url": "http://localhost:8080/<id>"
}
```

### Use Short URL

Doing a `GET http://localhost:8080/<id>` will cause a redirect to original URL

## Configure

* `BASE_URL` - base url for shortened URL
* `URL_MAX_LENGTH` - Max acceptable request URL length. Default 2000

## Assumptions
* Only `http` and `https` schemes are valid
* No ratelimit applied
* No authentication to access
