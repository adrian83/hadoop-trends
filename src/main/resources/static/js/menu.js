
class Link {
	constructor(id, text, url) {
		this.id = id;
		this.text = text;
		this.url = url;
	}
	
	isActive(act) {
		return this.id == act;
	}
	
	draw() {
		return "<li><a href=\"" + this.url + "\">" + this.text + "</a></li>";
	}
	
	drawActive() {
		return "<li class=\"active\"><a href=\"" + this.url + "\">" + this.text + "</a></li>";
	}
}

var links = [
	new Link("hashtags", "popular hashtags", "/view/hashtags"), 
	new Link("retwitts", "most retwitted", "/view/retwitts"),
	new Link("favorites", "most favorites", "/view/favorites"),
	new Link("replies", "most replied", "/view/replies")];


class Menu {
  constructor(active, container) {
    this.active = active;
    this.container = container;
  }
  
  draw() {
	  var html = "<div class=\"bs-example\" data-example-id=\"inverted-navbar\">" +
		"<nav class=\"navbar navbar-inverse\">" +
			"<div class=\"container-fluid\">" +
				"<div class=\"collapse navbar-collapse\" id=\"bs-example-navbar-collapse-9\">" +
					"<ul class=\"nav navbar-nav\">";
	  for(var i = 0; i < links.length; i++) {
		  var link = links[i];
		  html += link.isActive(this.active) ? link.drawActive() : link.draw();
	  }

	  html += "</ul></div></div></nav></div>";
	  document.getElementById(this.container).innerHTML = html;
  }
  
}
