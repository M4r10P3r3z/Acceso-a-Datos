package proyecto.modelo;

public record Contrato(String nif, String adjudicatario, String objetoGenerico, String objeto,
                       String fecha, String importe, String proveedoresConsultados, String tipoContrato) {


    @Override
    public String toString() {
        return "Contrato{" +
                "nif='" + nif + '\'' +
                ", adjudicatario='" + adjudicatario + '\'' +
                ", objetoGenerico='" + objetoGenerico + '\'' +
                ", objeto='" + objeto + '\'' +
                ", fecha='" + fecha + '\'' +
                ", importe='" + importe + '\'' +
                ", tipoContrato='" + tipoContrato + '\'' +
                '}';
    }
}

