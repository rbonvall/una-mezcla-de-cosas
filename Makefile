ENV = ./env
PIP = $(ENV)/bin/pip
AWS = $(ENV)/bin/aws
PANFLUTE = $(ENV)/bin/panflute

help:
	@echo "Usage:"
	@echo "  make all"
	@echo "  make env"
	@echo "  make sync"

.PHONY: all
all:
	find . -mindepth 2 -name Makefile -printf '%h\n' | while read DIR; do make -C "$$DIR"; done

$(ENV) $(PIP):
	python3 -m venv $(ENV)

.PHONY: dependencies
dependencies: $(AWS) $(PANFLUTE)

$(AWS) $(PANFLUTE): $(PIP)
	$< install awscli panflute

.PHONY: sync
sync: $(AWS)
	$< s3 sync mez.cl s3://mez.cl

