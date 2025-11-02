import React from "react";

export default function LoanStepper({ currentStep }) {
  const steps = ["Loan Details", "Review & Submit"];

  return (
    <div className="flex items-center justify-between w-full max-w-3xl mx-auto mb-8">
      {steps.map((label, index) => {
        const stepNumber = index + 1;
        const isCompleted = currentStep > stepNumber;
        const isActive = currentStep === stepNumber;

        return (
          <div key={index} className="flex flex-col items-center relative w-full">
            {/* Line between steps */}
            {index !== 0 && (
              <div
                className={`absolute top-5 left-[-50%] w-full h-[2px] ${
                  isCompleted ? "bg-green-500" : "bg-gray-300"
                }`}
              ></div>
            )}

            {/* Step Circle */}
            <div
              className={`w-10 h-10 rounded-full flex items-center justify-center font-semibold text-sm z-10 transition-all duration-300
                ${isCompleted ? "bg-green-500 text-white" : ""}
                ${isActive ? "bg-white border border-blue-500 text-blue-500 shadow-md shadow-blue-300/50" : ""}
                ${!isCompleted && !isActive ? "bg-gray-300 text-gray-600" : ""}
              `}
            >
              {stepNumber}
            </div>

            {/* Label */}
            <p
              className={`mt-2 text-sm ${
                isActive ? "text-blue-400 font-medium" : "text-gray-600"
              }`}
            >
              {label}
            </p>
          </div>
        );
      })}
    </div>
  );
}
