package modelo.test;

import modelo.dao.BilleteraDAO;
import modelo.dao.MovimientoDAO;
import modelo.entidades.Billetera;
import modelo.entidades.Movimiento;
import modelo.entidades.TipoMovimiento;
import modelo.entidades.UsuarioRegistrado;

import java.time.LocalDateTime;

public class DAODemo {
    public static void main(String[] args) {
        // Configurar: asegúrate de que DBUtil apunta a tu base de datos y las tablas existan.
        UsuarioRegistrado user = new UsuarioRegistrado();
        user.setId(1); // usa un id existente en tu tabla usuario

        BilleteraDAO billeteraDAO = new BilleteraDAO();
        MovimientoDAO movimientoDAO = new MovimientoDAO();

        // Buscar billetera
        Billetera b = billeteraDAO.buscarPorUsuario(user.getId());
        System.out.println("Billetera antes: " + b);

        double montoRecarga = 50.0;
        boolean ok = billeteraDAO.recargarBilletera(montoRecarga, user);
        System.out.println("Recarga realizada: " + ok);

        // Registrar movimiento
        if (b != null && ok) {
            Movimiento mov = new Movimiento();
            mov.setIdBilletera(b.getId());
            mov.setTipo(TipoMovimiento.RECARGA);
            mov.setMonto(montoRecarga);
            mov.setFecha(LocalDateTime.now());

            boolean movOk = movimientoDAO.guardarMovimiento(mov);
            System.out.println("Movimiento guardado: " + movOk);

            // Volver a consultar la billetera
            Billetera b2 = billeteraDAO.buscarPorUsuario(user.getId());
            System.out.println("Billetera despues: " + b2);
        }
    }
}
