package starter

import didawn.SpService

class SpFilters {

    SpService spService

    def filters = {
        all(controller: '*', action: '*') {
            before = {
                if (!session.spUri) {
                    session.spUri = spService.buildAuthorizeUri().toString()
                }
            }
            after = { Map model ->
            }
            afterView = { Exception e ->

            }
        }
    }
}
