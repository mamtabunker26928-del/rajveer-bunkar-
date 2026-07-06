package com.example.data.model

data class FormulaItem(
    val title: String,
    val expression: String,
    val description: String,
    val note: String
)

data class FormulaCategory(
    val categoryName: String,
    val subject: String, // "Physics", "Chemistry", "Maths"
    val formulas: List<FormulaItem>
)

object FormulaDataProvider {
    val categories = listOf(
        FormulaCategory(
            categoryName = "Kinematics & Mechanics",
            subject = "Physics",
            formulas = listOf(
                FormulaItem(
                    title = "Equations of Motion (Constant Acceleration)",
                    expression = "v = u + at \ns = ut + ½at² \nv² = u² + 2as",
                    description = "Fundamental equations relating velocity (v), initial velocity (u), acceleration (a), time (t), and displacement (s).",
                    note = "Sirf constant acceleration ke case me valid hai!"
                ),
                FormulaItem(
                    title = "Projectile Motion: Time of Flight, Max Height, Range",
                    expression = "T = (2u sinθ) / g \nH = (u² sin²θ) / 2g \nR = (u² sin2θ) / g",
                    description = "Formulas for a projectile launched at an angle θ with speed u.",
                    note = "Max range happens when θ = 45°"
                ),
                FormulaItem(
                    title = "Work-Energy Theorem",
                    expression = "W_net = ΔK = K_final - K_initial",
                    description = "The net work done by all forces equals the change in kinetic energy of the system.",
                    note = "Applicable to both conservative and non-conservative forces!"
                )
            )
        ),
        FormulaCategory(
            categoryName = "Electrodynamics",
            subject = "Physics",
            formulas = listOf(
                FormulaItem(
                    title = "Coulomb's Law",
                    expression = "F = k · (|q₁ · q₂|) / r²",
                    description = "Electrostatic force between two point charges q₁ and q₂ separated by distance r. k = 1 / (4πε₀) ≈ 9 × 10⁹ N·m²/C².",
                    note = "Like charges repel, opposite charges attract!"
                ),
                FormulaItem(
                    title = "Gauss's Law",
                    expression = "∮ E · dA = Q_in / ε₀",
                    description = "The electric flux through any closed surface is equal to the net charge enclosed divided by the permittivity of free space.",
                    note = "Highly useful for symmetric charge distributions!"
                )
            )
        ),
        FormulaCategory(
            categoryName = "Physical Chemistry",
            subject = "Chemistry",
            formulas = listOf(
                FormulaItem(
                    title = "Ideal Gas Equation",
                    expression = "P·V = n·R·T",
                    description = "Relates pressure (P), volume (V), number of moles (n), ideal gas constant (R), and absolute temperature (T).",
                    note = "R = 8.314 J/(mol·K) or 0.0821 L·atm/(mol·K)."
                ),
                FormulaItem(
                    title = "Gibbs Free Energy & Spontaneity",
                    expression = "ΔG = ΔH - T·ΔS",
                    description = "Calculates Gibbs Free Energy change where ΔH is enthalpy change and ΔS is entropy change.",
                    note = "ΔG < 0 means reaction is Spontaneous (Automatic)!"
                ),
                FormulaItem(
                    title = "Arrhenius Equation (Chemical Kinetics)",
                    expression = "k = A · e^(-E_a / R·T)",
                    description = "Defines the temperature dependence of reaction rates, where E_a is activation energy and A is frequency factor.",
                    note = "High Activation Energy means slower reaction!"
                )
            )
        ),
        FormulaCategory(
            categoryName = "Calculus Essentials",
            subject = "Maths",
            formulas = listOf(
                FormulaItem(
                    title = "Standard Derivatives",
                    expression = "d/dx(sin x) = cos x \nd/dx(ln x) = 1/x \nd/dx(e^x) = e^x",
                    description = "Essential derivatives for quick calculations.",
                    note = "Chain rule lagana mat bhulna!"
                ),
                FormulaItem(
                    title = "Standard Integrals",
                    expression = "∫(1 / (x² + a²)) dx = (1/a) tan⁻¹(x/a) + C \n∫ e^x dx = e^x + C",
                    description = "Common indefinite integration results.",
                    note = "Integrate by parts formula: ∫u v dx = u∫v dx - ∫(u' ∫v dx) dx"
                )
            )
        ),
        FormulaCategory(
            categoryName = "Coordinate Geometry & Algebra",
            subject = "Maths",
            formulas = listOf(
                FormulaItem(
                    title = "Quadratic Equations Roots",
                    expression = "x = [-b ± √(b² - 4ac)] / 2a",
                    description = "Roots of standard quadratic ax² + bx + c = 0.",
                    note = "D = b² - 4ac defines the nature of roots (Real or Imaginary)!"
                ),
                FormulaItem(
                    title = "Euler's Formula (Complex Numbers)",
                    expression = "e^(iθ) = cosθ + i sinθ",
                    description = "Relates trigonometric functions to complex exponential powers.",
                    note = "i² = -1. Euler form complex geometry problems ko easy bana deta hai!"
                )
            )
        )
    )
}
