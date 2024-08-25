"""
The representation of a database user in the transport.

Author: Jonathan Uhler
"""


from flask_login import UserMixin


class User(UserMixin):
    """
    A database user.

    Attributes:
     id (str): the unique identifier for this user.
    """

    def __init__(self, sub: str):
        """
        Constructs a new `User`.

        Arguments:
         sub (str): the unique identifier for this user.
        """

        self.id = sub
