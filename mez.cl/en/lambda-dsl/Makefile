index.html: index.md template.html lambda.sc
	pandoc --standalone --template=template.html $< -o $@ -F panflute

.PHONY: test
test: lambda.sc
	amm $<

watch:
	while inotifywait -qre close_write .; do make; done
