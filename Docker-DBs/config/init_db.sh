#!/bin/bash
set -o errexit

readonly REQUIRED_ENV_VARS=(
  "SSO_USER"
  "SSO_USER_PASSWORD"
  "SSO_DB"
  "POSTGRES_USER")

main() {
  check_env_vars_set
  init_user_and_db
}

check_env_vars_set() {
  for required_env_var in ${REQUIRED_ENV_VARS[@]}; do
    if [[ -z "${!required_env_var}" ]]; then
      echo "Error:
    Environment variable '$required_env_var' not set.
    Make sure you have the following environment variables set:
      ${REQUIRED_ENV_VARS[@]}
Aborting."
      exit 1
    fi
  done
}

init_user_and_db() {
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
     CREATE USER $SSO_USER WITH PASSWORD '$SSO_USER_PASSWORD';
     CREATE DATABASE "$SSO_DB";
     GRANT ALL PRIVILEGES ON DATABASE "$SSO_DB" TO $SSO_USER;
EOSQL

  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" "$SSO_DB" <<-EOSQL
     GRANT ALL ON SCHEMA public TO $SSO_USER;
EOSQL
}

main "$@"
