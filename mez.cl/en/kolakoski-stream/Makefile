TEMPLATE = ../../../templates/template.html
CODE = kola.sc

index.html: index.md $(TEMPLATE) $(CODE)
	pandoc --standalone --template=$(TEMPLATE) $< -o $@ -F panflute

.PHONY: test
test: $(CODE)
	amm $<

.PHONY: clean
clean:
	rm -vi index.html

watch:
	while inotifywait -qre close_write .; do make; done
