package com.example.indoorlocalizationv2.logic;

import com.example.indoorlocalizationv2.models.BLEPosition;

public class TrilaterationLogic {
    private BLEPosition bottomLeftAnchor;
    private BLEPosition bottomFrontAnchor;
    private BLEPosition bottomRightAnchor;
    private BLEPosition topMiddleAnchor;

    public TrilaterationLogic(
            BLEPosition bottomLeftAnchor,
            BLEPosition bottomFrontAnchor,
            BLEPosition bottomRightAnchor,
            BLEPosition topMiddleAnchor) {
        this.bottomLeftAnchor = bottomLeftAnchor;
        this.bottomFrontAnchor = bottomFrontAnchor;
        this.bottomRightAnchor = bottomRightAnchor;
        this.topMiddleAnchor = topMiddleAnchor;
    }

    public BLEPosition calculateBeaconPosition(){
        BLEPosition beaconPosition = new BLEPosition();

        float coordinateX = this.calculateBeaconCoordinateX();
        beaconPosition.setX(coordinateX);

        float coordinateY = this.calculateBeaconCoordinateY(coordinateX);
        beaconPosition.setY(coordinateY);

        beaconPosition.setZ(this.calculateBeaconCoordinateZ(coordinateX, coordinateY));
        return beaconPosition;
    }

    /**
     * Calculates the beacon's X coordinate.
     * Formula:
     * x = (R1^2 - R2^2 + X2^2) / 2 * X2
     * @return Beacon X coordinate.
     */
    private float calculateBeaconCoordinateX() {
        return (float)
                (Math.pow(bottomLeftAnchor.getDistance(), 2)
                        - Math.pow(bottomRightAnchor.getDistance(), 2)
                        + Math.pow(bottomRightAnchor.getX(), 2)
                ) / 2 * bottomRightAnchor.getX();
    }

    /**
     * Calculates the beacon's Y coordinate.
     * Formula:
     * y = (R1^2 - R3^2 + X3^2 + Y3^2 - (2 * X3 * calculatedX)) / 2 * Y3
     * @param calculatedCoordinateX The calculated X coordinate.
     * @return Beacon Y coordinate.
     */
    private float calculateBeaconCoordinateY(float calculatedCoordinateX) {
        return (float)
                (Math.pow(bottomLeftAnchor.getDistance(), 2)
                        - Math.pow(bottomFrontAnchor.getDistance(), 2)
                        + Math.pow(bottomFrontAnchor.getX(), 2)
                        + Math.pow(bottomFrontAnchor.getY(), 2)
                        - (2 * bottomFrontAnchor.getX() * calculatedCoordinateX)
                ) / 2 * bottomFrontAnchor.getY();
    }

    /**
     * Calculates the beacon's Z coordinate.
     * Formula:
     * z = sqrt(R1^2 - calculatedX^2 - calculatedY^2)
     * @param calculatedCoordinateX The calculated X coordinate.
     * @param calculatedCoordinateY The calculated Y coordinate.
     * @return Beacon Z coordinate.
     */
    private float calculateBeaconCoordinateZ(float calculatedCoordinateX, float calculatedCoordinateY) {
        return (float)
                (Math.sqrt(
                        Math.pow(bottomLeftAnchor.getDistance(), 2)
                        - Math.pow(calculatedCoordinateX, 2)
                        - Math.pow(calculatedCoordinateY, 2)
                ));
    }
}
