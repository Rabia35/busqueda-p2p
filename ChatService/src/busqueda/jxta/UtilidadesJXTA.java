
package busqueda.jxta;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.Module;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

/**
 *
 * @author almunoz
 */
public abstract class UtilidadesJXTA {
    public static final String ATRIBUTO_BUSQUEDA = "Name";
    public static final long PIPE_TIMEOUT = 5 * 1000; // 5 segundos
    public static final int DELAY_BUSQUEDA = 10 * 1000; // 10 segundos

    public static void crearPeer(NetworkManager manager, String nombre) {
        PeerID peerID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, nombre.getBytes());
        manager.setPeerID(peerID);
    }

    public static PeerGroup crearGrupo(PeerGroup netPeerGroup, String nombre, String descripcion) {
        try {
            PeerGroupID peerGroupID = IDFactory.newPeerGroupID(nombre.getBytes());
            ModuleImplAdvertisement moduleImpAdv = netPeerGroup.getAllPurposePeerGroupImplAdvertisement();
            // The creation includes local publishing
            System.out.println("Creando el nuevo PeerGroup: '" + nombre + "'");
            PeerGroup grupo = netPeerGroup.newGroup(peerGroupID, moduleImpAdv, nombre, descripcion);
            return grupo;
        } catch (Exception ex) {
            System.out.println("Exception: No se pudo crear el PeerGroup.");
            return null;
        }
    }

    public static void iniciarGrupo(PeerGroup grupo) {
        if (Module.START_OK == grupo.startApp(new String[0])) {
            System.out.println("El PeerGroup '" + grupo.getPeerGroupName() + "' se inicio correctamente.");
        } else {
            System.out.println("El PeerGroup '" + grupo.getPeerGroupName() + "' no se pudo iniciar.");
        }
    }

    public static void terminarGrupo(PeerGroup grupo) {
        System.out.println("El PeerGroup '" + grupo.getPeerGroupName() + "' se termino correctamente.");
    }

    public static PipeAdvertisement crearPipeAdvertisementFromString(String advString) {
        try {
            ByteArrayInputStream advStream = new ByteArrayInputStream(advString.getBytes());
            XMLDocument xml = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, advStream);
            PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(xml);
            return advertisement;
        } catch (IOException ex) {
            System.out.println("IOException: No se pudo crear el PipeAdvertisement.");
            return null;
        }
    }

    public static PipeAdvertisement crearPipeAdvertisement(PeerGroup grupo, String nombre, String descripcion) {
        PipeID pipeID = (PipeID) IDFactory.newPipeID(grupo.getPeerGroupID());
        PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        advertisement.setPipeID(pipeID);
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName(nombre);
        advertisement.setDescription(descripcion);
        return advertisement;
    }

    public static InputPipe crearInputPipe(PeerGroup grupo, PipeAdvertisement advertisement, PipeMsgListener listener) {
        try {
            InputPipe pipe = grupo.getPipeService().createInputPipe(advertisement, listener);
            return pipe;
        } catch (IOException ex) {
            System.out.println("IOException: No se pudo crear el InputPipe.");
            return null;
        }
    }

    public static OutputPipe crearOuputPipe(PeerGroup grupo, PipeAdvertisement advertisement) {
        try {
            OutputPipe pipe = grupo.getPipeService().createOutputPipe(advertisement, PIPE_TIMEOUT);
            return pipe;
        } catch (IOException ex) {
            System.out.println("IOException: No se pudo crear el OutputPipe.");
            return null;
        }
    }

    public static void publicarAdvertisement(PeerGroup grupo, Advertisement advertisement) {
        try {
            grupo.getDiscoveryService().publish(advertisement);
            grupo.getDiscoveryService().remotePublish(advertisement);
        } catch (IOException ex) {
            System.out.println("IOException: No se pudo publicar el advertisement.");
        }
    }

    public static void publicarAdvertisementLocal(PeerGroup grupo, Advertisement advertisement) {
        try {
            grupo.getDiscoveryService().publish(advertisement);
        } catch (IOException ex) {
            System.out.println("IOException: No se pudo publicar el advertisement.");
        }
    }

    public static void publicarAdvertisementRemoto(PeerGroup grupo, String peerID, Advertisement advertisement) {
        grupo.getDiscoveryService().remotePublish(peerID, advertisement);
    }

    public static void eliminarAdvertisement(PeerGroup grupo, Advertisement advertisement) {
        try {
            grupo.getDiscoveryService().flushAdvertisement(advertisement);
        } catch (IOException ex) {
            System.out.println("IOException: No se pudo eliminar el advertisement.");
        }
    }

}
