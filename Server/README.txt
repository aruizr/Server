Obrir config.properties

Si el teu router no té els ports oberts i només vols fer una prova 
del funcionament del Servidor i Client en el mateix PC recomanem la següent configuració:

	serverPort = 4444
	localTest = true
	clientRunningInSameMachineAsServer = true


Si vols fer una prova del Server amb Clients connectant-se des de xarxes externes i tenint el port 4444 de router obert
recommanem la següent configuració:

	serverPort = 4444
	localTest = false
	clientRunningInSameMachineAsServer = (true si executarás el Client en el mateix PC que el Server, sinó false)