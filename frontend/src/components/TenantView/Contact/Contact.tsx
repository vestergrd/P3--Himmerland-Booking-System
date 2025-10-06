import React from 'react';
import { Container, Row, Col, Card } from 'react-bootstrap';

const Contact: React.FC = () => {
  return (
    <Container className="my-5">
      <Row className="justify-content-center">
        <Col md={8}>
          <Card className="shadow-sm">
            <Card.Header className="text-white text-center" style={{ backgroundColor: '#28a745' }}>
              <h3>Kontakt Os</h3>
            </Card.Header>
            <Card.Body>
              <Row className="mb-4">
                <Col md={6}>
                  <h5>Servicekontor</h5>
                  <p>
                    Himmerland Boligforening<br />
                    Fredrik Bajers Vej 154A<br />
                    9220 Aalborg<br />
                    Danmark
                  </p>
                </Col>
                <Col md={6}>
                  <h5>Kontakt Information</h5>
                  <p>
                    Phone: <a href="tel:+4598158422">+45 98 15 84 22</a><br />
                    Email: <a href="mailto:kanalkvarteret@abhim.dk">kanalkvarteret@abhim.dk</a><br />
                    Website: <a href="https://www.abhim.dk/" target="_blank" rel="noopener noreferrer">www.abhim.dk</a>
                  </p>
                </Col>
              </Row>
              <Row>
                <Col>
                  <h5>Business Hours</h5>
                  <p>
                    Mandag - Fredag: 7:00 - 7:30  og 11:00 - 12:00<br />
                    Lørdag - Søndag: Lukket
                    <br />
                    <br />
                    Uden for normal kontortid, kan der sendes en mail til varmemesterkontoret.<br />
                    Ved uopsættelig skade benyttes <a href="tel:+4522282024">+45 22 28 20 24</a>
                  </p>
                </Col>
              </Row>
            </Card.Body>
            <Card.Footer className="text-center text-muted">
              {"Himmerland Boligforening (Kanalkvarteret)"}
            </Card.Footer>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Contact;
