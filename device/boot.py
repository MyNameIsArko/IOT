import machine


if machine.reset_cause() != machine.SOFT_RESET:
    import pairing
    # Soft reset doesn't restart WLAN
    pairing.enter_pairing()

import webrepl
webrepl.start()