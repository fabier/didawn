package didawn

import org.springframework.security.access.annotation.Secured

@Secured(['permitAll'])
class AlbumController {

    def show(String id) {
        render view: "index"
    }
}
