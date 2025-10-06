import React, { useState, useEffect } from 'react';

interface PlaceholderProps {
  width?: string;
  height?: string;
  color?: string;
  text?: string;
  loadingDuration?: number; // Optional prop to set how long the placeholder should show loading
}

const Placeholder: React.FC<PlaceholderProps> = ({
  width = '100%',
  height = '1rem',
  color = 'light',
  text = '',
  loadingDuration = 1000, // Default to 2 seconds
}) => {
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Set a timeout to simulate loading
    const timer = setTimeout(() => {
      setIsLoading(false);
    }, loadingDuration);

    // Clear the timer if the component unmounts
    return () => clearTimeout(timer);
  }, [loadingDuration]);

  return (
    <div className="mb-3" style={{ width, height }}>
      {isLoading ? (
        <div className={`placeholder bg-${color}`} style={{ width, height }}></div>
      ) : (
        <span>{text}</span>
      )}
    </div>
  );
};

export default Placeholder;