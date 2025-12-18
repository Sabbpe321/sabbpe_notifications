package com.giftcard.emails.giftcardemailtemplates.template;





	import com.github.jknack.handlebars.Handlebars;
	import com.github.jknack.handlebars.Template;
	import org.springframework.stereotype.Component;


	import java.io.IOException;
	import java.util.concurrent.ConcurrentHashMap;
	import java.util.concurrent.ConcurrentMap;


	@Component
	public class TemplateCache {
	private final ConcurrentMap<String, Template> cache = new ConcurrentHashMap<>();
	private final Handlebars handlebars;


	public TemplateCache(Handlebars handlebars) {
	this.handlebars = handlebars;
	}


	public Template getOrCompile(String templateId, String templateBody) {
	return cache.computeIfAbsent(templateId, id -> {
	try {
	return handlebars.compileInline(templateBody);
	} catch (IOException e) {
	throw new RuntimeException(e);
	}
	});
	}


	public void invalidate(String templateId) { cache.remove(templateId); }
	}

