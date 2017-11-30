index.html: index.md template.html typeclasses.sc
	pandoc --standalone --template=template.html $< -o $@ -F panflute

test: typeclasses.sc
	amm $<

watch:
	while inotifywait -qre close_write .; do make; done

clean:
	rm -v index.html

.PHONY: test watch clean
