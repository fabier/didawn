<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Didawn</title>
</head>

<body>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <h1>${playlist.name}</h1>
        </div>
    </div>

    <g:each in="${tracks}">
        <div class="row">
            <div class="col-md-12">
                <g:link controller="track" action="show" id="${it.track.id}">
                    ${it.track.artists.first().name} - ${it.track.name}
                </g:link>
            </div>
        </div>
    </g:each>
</div>
</body>
</html>
