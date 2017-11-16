package didawn

import org.springframework.security.access.annotation.Secured

@Secured(['permitAll'])
class CallbackController {

    SpService spService

    def index() {
        String accessToken = spService.handleCallbackRequest(request)
        session.setAttribute("accessToken", accessToken)
        redirect controller: "public"
    }
}
