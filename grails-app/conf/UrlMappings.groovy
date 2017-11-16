class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?"()

        "/"(controller: "public")
        "500"(view:'/error')
	}
}
