index.html: index.md template.html
	pandoc --standalone --template=template.html $< -o $@

watch:
	while inotifywait -qre close_write .; do make; done

clean:
	rm -vi index.html

.PHONY: watch clean
