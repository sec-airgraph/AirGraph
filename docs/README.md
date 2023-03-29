# Documents

## Install

* install venv

  ```sh
  $ sudo -E apt install python3.8-venv
  ```

* enable venv and install requirements

  ```sh
  $ python -m venv env
  $ source ./env/bin/activate
  $ pip install -r requirements.txt
  ```

## Generate

```sh
(env) $ make html
```

> See `make help` for other options.

## Preview

```sh
(env) $ python -m http.server --directory ./build/html <port>
```

> please access http://<your_ip>:<port>/