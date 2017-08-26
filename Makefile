ENV = ./env
PIP = $(ENV)/bin/pip
AWS = $(ENV)/bin/aws

help:
	@echo "Usage:"
	@echo "  make env"
	@echo "  make sync"

$(ENV) $(PIP):
	python3 -m venv $(ENV)

$(AWS): $(PIP)
	$< install awscli

.PHONY: sync
sync: $(AWS)
	$< s3 sync mez.cl s3://mez.cl

