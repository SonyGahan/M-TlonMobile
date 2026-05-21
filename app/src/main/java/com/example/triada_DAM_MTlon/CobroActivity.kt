package com.example.triada_DAM_MTlon

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.triada_DAM_MTlon.database.Datos
import com.example.triada_DAM_MTlon.model.SocioDTO

class CobroActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobro)

        val dniRecibido = intent.getStringExtra("DNI") ?: ""
        var tipoUsuarioActual = "Socio"

        val tvNombreCompleto = findViewById<TextView>(R.id.tvNombreCompleto)
        val tvDniCobro = findViewById<TextView>(R.id.tvDniCobro)
        val tvEmailCobro = findViewById<TextView>(R.id.tvEmailCobro)
        val tvTelefonoCobro = findViewById<TextView>(R.id.tvTelefonoCobro)
        val etMonto = findViewById<EditText>(R.id.etMonto)

        val spModoPago = findViewById<Spinner>(R.id.spModoPago)
        val llContenedorCuotas = findViewById<LinearLayout>(R.id.llContenedorCuotas)
        val spCuotas = findViewById<Spinner>(R.id.spCuotas)

        val btnRegistrarPago = findViewById<Button>(R.id.btnRegistrarPago)
        val btnNuevaConsulta = findViewById<Button>(R.id.btnNuevaConsultaCobro)
        val btnVolverMenu = findViewById<Button>(R.id.btnVolverMenu)

        val db = Datos(this)

        val listaCuotas = arrayOf("1 cuota", "3 cuotas", "6 cuotas", "12 cuotas")
        val adapterCuotas = ArrayAdapter(this, R.layout.spinner_item, listaCuotas)
        adapterCuotas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCuotas.adapter = adapterCuotas

        val socioObjeto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("SOCIO_OBJETO", SocioDTO::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("SOCIO_OBJETO") as? SocioDTO
        }

        if (socioObjeto != null) {
            tipoUsuarioActual = socioObjeto.tipoUsuario
            tvNombreCompleto.text = "${socioObjeto.nombre} ${socioObjeto.apellido}"
            tvDniCobro.text = "DNI: ${socioObjeto.dni}"
            tvEmailCobro.text = "Email: ${socioObjeto.email}"
            tvTelefonoCobro.text = "Teléfono: ${socioObjeto.telefono}"
        } else if (dniRecibido.isNotEmpty()) {
            val socio = db.consultarEstadoDNI(dniRecibido)
            if (socio != null) {
                tipoUsuarioActual = socio.tipoUsuario
                tvNombreCompleto.text = "${socio.nombre} ${socio.apellido}"
                tvDniCobro.text = "DNI: $dniRecibido"
                tvEmailCobro.text = "Email: ${socio.email}"
                tvTelefonoCobro.text = "Teléfono: ${socio.telefono}"
            }
        }

        val opcionesPago: Array<String> = if (tipoUsuarioActual.equals("No Socio", ignoreCase = true)) {
            arrayOf("Efectivo", "Tarjeta de Débito")
        } else {
            arrayOf("Efectivo", "Tarjeta de Débito", "Tarjeta de Crédito", "Transferencia")
        }

        val adapterPago = ArrayAdapter(this, R.layout.spinner_item, opcionesPago)
        adapterPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spModoPago.adapter = adapterPago

        spModoPago.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = opcionesPago[position]
                if (seleccion == "Tarjeta de Crédito") {
                    llContenedorCuotas.visibility = View.VISIBLE
                } else {
                    llContenedorCuotas.visibility = View.GONE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnRegistrarPago.setOnClickListener {
            val montoTxt = etMonto.text.toString().trim()
            var modoPago = spModoPago.selectedItem.toString()
            val montoNum = montoTxt.toDoubleOrNull()

            if (montoNum != null && dniRecibido.isNotEmpty()) {
                val fechaHoraActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss"))

                if (tipoUsuarioActual.equals("Socio", ignoreCase = true)) {
                    val nuevoVencimiento = LocalDateTime.now().plusMonths(1).toLocalDate().toString()
                    db.actualizarVencimiento(dniRecibido, nuevoVencimiento)
                }

                if (modoPago == "Tarjeta de Crédito") {
                    val cuotasSeleccionadas = spCuotas.selectedItem.toString()
                    modoPago = "Crédito ($cuotasSeleccionadas)"
                }

                val mensaje = db.insertarPago(dniRecibido, montoNum, modoPago, fechaHoraActual)
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

                if (mensaje == "¡Pago exitoso!") {
                    val intentTicket = Intent(this, ComprobanteActivity::class.java)
                    intentTicket.putExtra("MONTO", montoTxt)
                    intentTicket.putExtra("DNI", dniRecibido)
                    intentTicket.putExtra("FECHA", fechaHoraActual)
                    intentTicket.putExtra("METODO", modoPago)
                    startActivity(intentTicket)
                    finish()
                }

            } else {
                Toast.makeText(this, "Complete todos los campos del pago", Toast.LENGTH_SHORT).show()
            }
        }

        btnNuevaConsulta.setOnClickListener {
            val intentConsulta = Intent(this, VerificacionActivity::class.java)
            intentConsulta.putExtra("FLUJO", "PAGOS")
            intentConsulta.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intentConsulta)
            finish()
        }

        btnVolverMenu.setOnClickListener {
            val intentInicio = Intent(this, MenuActivity::class.java)
            startActivity(intentInicio)
            finish()
        }
    }
}