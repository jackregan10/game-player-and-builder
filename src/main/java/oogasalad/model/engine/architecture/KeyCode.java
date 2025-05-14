package oogasalad.model.engine.architecture;

/**
 * The keycode for binding. This should be mapped from actual physical keys.
 *
 * @author Hsuan-Kai Liao, Christian Bepler
 */
public enum KeyCode {
  NONE(0),

  A(65),
  B(66),
  C(67),
  D(68),
  E(69),
  F(70),
  G(71),
  H(72),
  I(73),
  J(74),
  K(75),
  L(76),
  M(77),
  N(78),
  O(79),
  P(80),
  Q(81),
  R(82),
  S(83),
  T(84),
  U(85),
  V(86),
  W(87),
  X(88),
  Y(89),
  Z(90),

  DIGIT_0(48),
  DIGIT_1(49),
  DIGIT_2(50),
  DIGIT_3(51),
  DIGIT_4(52),
  DIGIT_5(53),
  DIGIT_6(54),
  DIGIT_7(55),
  DIGIT_8(56),
  DIGIT_9(57),

  ENTER(10),
  ESCAPE(27),
  SPACE(32),
  BACKSPACE(8),
  TAB(9),
  SHIFT(16),
  CONTROL(17),
  ALT(18),
  DELETE(127),

  LEFT(37),
  RIGHT(39),
  UP(38),
  DOWN(40);

  private final int keyCode;

  /**
   * Constructor for KeyCode enum.
   *
   * @param keyCode The corresponding key code.
   */
  KeyCode(int keyCode) {
    this.keyCode = keyCode;
  }

  /**
   * Get the integer value of the key code.
   */
  public int getValue() {
    return keyCode;
  }

  /**
   * Finds a KeyCode by its integer value.
   *
   * @param keyCode The integer key code.
   * @return The matching KeyCode, or null if not found.
   */
  public static KeyCode fromKeyCode(int keyCode) {
    for (KeyCode kc : values()) {
      if (kc.keyCode == keyCode) {
        return kc;
      }
    }
    return null;
  }
}
