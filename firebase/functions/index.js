const functions = require('firebase-functions');

const admin = require('firebase-admin');

admin.initializeApp();

exports.notificacionNuevaTareaEnGrupo = functions.database.ref('/Tareas/{tareaUid}')
    .onCreate(async (change, context) => {
        const tareaUid = context.params.tareaUid;

        const tarea = (await admin.database().ref(`/Tareas/${tareaUid}`).once('value')).toJSON();

        const grupoId = tarea.grupo;

        if (!grupoId || grupoId.length < 1) {
            return console.log('Tarea no tiene grupo.');
        }

        const grupo = (await admin.database().ref(`/Grupos/${grupoId}`).once('value')).toJSON();

        const esEvento = tarea.ubicacion !== null && tarea.ubicacion !== undefined && tarea.ubicacion !== 'null' && tarea.ubicacion !== '';

        const payload = {
            notification: {
                title: `Nuev${esEvento ? 'o evento' : 'a tarea'} en el grupo ${grupo.nombre}`,
                body: `Se ha creado ${esEvento ? 'un nuevo evento' : 'una nueva tarea'} en el grupo ${grupo.nombre}`,
            }
        };

        console.log('Enviar notificación');

        return await admin.messaging().sendToTopic(`_group_${grupoId}`, payload);
    });

exports.notificacionUsuarioAgregadoAGrupo = functions.database.ref('/GrupoUsuarios/{grupoUsuarioId}')
    .onCreate(async (change, context) => {
        const grupoUsuarioId = context.params.grupoUsuarioId;

        const grupoUsuario = (await admin.database().ref(`/GrupoUsuarios/${grupoUsuarioId}`).once('value')).toJSON();
        const grupo = (await admin.database().ref(`/Grupos/${grupoUsuario.grupo}`).once('value')).toJSON();

        const payload = {
            notification: {
                title: `Añadido a grupo ${grupo.nombre}`,
                body: `Se le ha añadido al grupo ${grupo.nombre}`,
            }
        };

        return await admin.messaging().sendToTopic(`_user_${grupoUsuario.usuario}`, payload);
    });

exports.notificacionUsuarioBorradoDeGrupo = functions.database.ref('/GrupoUsuarios/{grupoUsuarioId}')
    .onDelete(async (change, context) => {
        const grupo = (await admin.database().ref(`/Grupos/${change._data.grupo}`).once('value')).toJSON();

        const payload = {
            notification: {
                title: `Eliminado de grupo ${grupo.nombre}`,
                body: `Se le ha borrado del grupo ${grupo.nombre}`,
            }
        };

        return await admin.messaging().sendToTopic(`_user_${change._data.usuario}`, payload);
    });

exports.notificacionTareaAsignadaUsuario = functions.database.ref('/Tareas/{tareaId}')
    .onWrite(async (change, context) => {
        const tareaId = context.params.tareaId;

        const usuarioId = context.auth.token.user_id;

        const tarea = (await admin.database().ref(`/Tareas/${tareaId}`).once('value')).toJSON();

        if (tarea.grupo === null || tarea.grupo === undefined || tarea.grupo === '' || tarea.grupo === 'null') {
            return;
        }

        const grupo = (await admin.database().ref(`/Grupos/${tarea.grupo}`).once('value')).toJSON();

        if (tarea.usuarioAsignado !== usuarioId) {
            const payload = {
                notification: {
                    title: `Tarea asignada.`,
                    body: `Ha sido asignado a la tarea ${tarea.nombre} en el grupo ${grupo.nombre}`,
                }
            };

            await admin.messaging().sendToTopic(`_user_${tarea.usuarioAsignado}`, payload);
        }       
    });

exports.notificacionTareaFinalizada = functions.database.ref('/Tareas/{tareaId}')
    .onWrite(async (change, context) => {
        const tareaId = context.params.tareaId;

        const tarea = (await admin.database().ref(`/Tareas/${tareaId}`).once('value')).toJSON();

        if (tarea.grupo === null || tarea.grupo === undefined || tarea.grupo === '' || tarea.grupo === 'null') {
            return;
        }

        const grupo = (await admin.database().ref(`/Grupos/${tarea.grupo}`).once('value')).toJSON();

        if (tarea.finalizada === true || tarea.finalizada === 'true') {
            const payload = {
                notification: {
                    title: `Tarea finalizada.`,
                    body: `La tarea ${tarea.nombre} en el grupo ${grupo.nombre} ha sido finalizada.`,
                }
            };

            await admin.messaging().sendToTopic(`_user_${tarea.usuarioAsignado}`, payload);
        }       
    });